(ns moon.schema.val-max
  (:require [moon.schema :as moon-schema]
            [clojure.schema :as schema]))

(defmethod moon-schema/malli-form :s/val-max
  [_ _]
  schema/v)
