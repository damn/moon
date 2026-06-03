(ns schema.malli-form.sound
  (:require [moon.schemas.malli-form :refer [malli-form]]))

(defmethod malli-form :s/sound [_ _schemas]
  :string)
