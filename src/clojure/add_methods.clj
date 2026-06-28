(ns clojure.add-methods
  (:import (clojure.lang MultiFn)))

(defn f! [multifn-var k->f]
  (doseq [[k fn-var] k->f]
    (MultiFn/.addMethod @multifn-var k fn-var)))
