(ns moon.schema.sound
  (:require [moon.malli :as malli]))

(defmethod malli/form :s/sound [_ _schemas]
  :string)
