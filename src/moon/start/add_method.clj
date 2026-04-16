(ns moon.start.add-method
  (:import (clojure.lang MultiFn)))

(defn step [multifn-var data]
  (doseq [[k function-var] data]
    (MultiFn/.addMethod @multifn-var k function-var)))
