(ns moon.schema.sound
  (:require [moon.schema :as schema]))

(defmethod schema/malli-form :s/sound [_ _schemas]
  :string)
