(ns google-cache.util)

(def debug? (= 1 (System/getenv "DEBUG")))

(defn now []
  (System/nanoTime))

(defn elapsed-milliseconds [from]
  (/ (- now from) 1000000.0))

