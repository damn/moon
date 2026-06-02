(ns schema.malli-form.sound
  (:require [moon.schemas :refer [malli-form]]))

(defmethod malli-form :s/sound [_ _schemas]
  :string)
