(ns clojure.add-methods
  (:require [clojure.lang.multifn :as multifn]))

(defn f! [multifn-var k->f]
  (doseq [[k fn-var] k->f]
    (multifn/add-method! @multifn-var k fn-var)))
