(ns clojure.val-max.validate
  (:require [clojure.malli.schema :as malli-schema]
            [clojure.schema :as schema]))

(defn f [val-max]
  (malli-schema/validate schema/v val-max))
