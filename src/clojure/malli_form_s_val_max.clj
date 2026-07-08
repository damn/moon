(ns clojure.malli-form-s-val-max
  (:require [clojure.malli-form :refer [malli-form]]
            [clojure.schema :as schema]))

(defmethod malli-form :s/val-max
  [_ _]
  schema/v)
