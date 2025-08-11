import json
import math
import time
from typing import List, Optional

from agno.tools import Toolkit  # Agno's documented base for custom toolkits


def _matmul(a: List[List[float]], b: List[List[float]]) -> List[List[float]]:
    if not a or not b:
        raise ValueError("Matrices must be non-empty.")
    n, m = len(a), len(a[0])
    if any(len(row) != m for row in a):
        raise ValueError("Matrix A rows have inconsistent length.")
    if len(b) != m:
        raise ValueError(f"Shape mismatch: A is {n}x{m}, B must be {m}xP.")
    p = len(b[0])
    if any(len(row) != p for row in b):
        raise ValueError("Matrix B rows have inconsistent length.")
    c = [[0.0] * p for _ in range(n)]
    for i in range(n):
        for k in range(m):
            aik = a[i][k]
            for j in range(p):
                c[i][j] += aik * b[k][j]
    return c


def _big_factorial(n: int) -> str:
    if n < 0:
        raise ValueError("n must be non-negative.")
    return str(math.factorial(n))


def _monte_carlo_pi(samples: int, seed: Optional[int] = None) -> float:
    import random
    if samples <= 0:
        raise ValueError("samples must be positive.")
    rnd = random.Random(seed)
    inside = 0
    for _ in range(samples):
        x, y = rnd.random(), rnd.random()
        if x * x + y * y <= 1.0:
            inside += 1
    return 4.0 * inside / samples


class HeavyCalculationTools(Toolkit):
    def __init__(self, timeout_sec: float = 20.0):
        # Register the instance methods as tools
        super().__init__(
            name="heavy_calc_tools",
            tools=[
                self.matmul,
                self.big_factorial,
                self.monte_carlo_pi,
                self.power_sum,
            ],
        )
        self.timeout_sec = float(timeout_sec)

    # Tool: Matrix multiplication
    def matmul(self, a_json: str, b_json: str) -> str:
        """
        Multiply two matrices.
        Inputs:
          - a_json: JSON-encoded 2D list (A)
          - b_json: JSON-encoded 2D list (B)
        Returns:
          - JSON string: {"result": [[...]], "shape": [n, m, p]}
        """
        t0 = time.time()
        try:
            a = json.loads(a_json)
            b = json.loads(b_json)
            res = _matmul(a, b)
            if time.time() - t0 > self.timeout_sec:
                return json.dumps({"error": "timeout"})
            n, m, p = len(a), len(a[0]), len(b[0])
            return json.dumps({"result": res, "shape": [n, m, p]})
        except Exception as e:
            return json.dumps({"error": str(e)})

    # Tool: Big factorial
    def big_factorial(self, n: int) -> str:
        """
        Compute n! (returned as a string).
        """
        t0 = time.time()
        try:
            res = _big_factorial(int(n))
            if time.time() - t0 > self.timeout_sec:
                return "error: timeout"
            return res
        except Exception as e:
            return f"error: {e}"

    # Tool: Monte Carlo pi
    def monte_carlo_pi(self, samples: int, seed: Optional[int] = None) -> str:
        """
        Estimate π with Monte Carlo.
        Returns JSON: {"pi": float, "samples": int, "duration_sec": float}
        """
        t0 = time.time()
        try:
            val = _monte_carlo_pi(int(samples), None if seed is None else int(seed))
            dur = time.time() - t0
            if dur > self.timeout_sec:
                return json.dumps({"error": "timeout"})
            return json.dumps({"pi": val, "samples": int(samples), "duration_sec": dur})
        except Exception as e:
            return json.dumps({"error": str(e)})

    # Tool: Power sum
    def power_sum(self, n: int, k: int) -> str:
        """
        Compute S = sum_{i=1..n} i^k.
        Returns the sum as a stringified integer.
        """
        t0 = time.time()
        try:
            n = int(n)
            k = int(k)
            if n < 0 or k < 0:
                return "error: n and k must be non-negative"
            total = 0
            for i in range(1, n + 1):
                total += pow(i, k)
                if (i & 0x3FFF) == 0 and time.time() - t0 > self.timeout_sec:
                    return "error: timeout"
            return str(total)
        except Exception as e:
            return f"error: {e}"
