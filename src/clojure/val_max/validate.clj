(ns clojure.val-max.validate
  (:require [clojure.malli-validate :refer [validate]]
            [clojure.schema :as schema]))

(defn f [val-max]
  (validate schema/v val-max))
