(ns moon.throwable
  (:require [clj-commons.pretty.repl :as pretty-repl]))

(let [print-level 3
      print-depth 24]
  (defn pretty-pst [throwable]
    (binding [*print-level* print-level]
      (pretty-repl/pretty-pst throwable print-depth))))
