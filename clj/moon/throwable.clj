(ns moon.throwable
  (:require [clj-commons.pretty.repl :as pretty-repl]))

(def print-level 3)
(def print-depth 24)

(defn pretty-pst [throwable]
  (binding [*print-level* print-level]
    (pretty-repl/pretty-pst throwable print-depth)))
