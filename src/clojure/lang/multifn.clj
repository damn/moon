(ns clojure.lang.multifn
  (:import (clojure.lang MultiFn)))

(defn add-method! [multifn k f]
  (MultiFn/.addMethod multifn k f))
