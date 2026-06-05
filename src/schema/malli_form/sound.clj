(ns schema.malli-form.sound
  (:require [clojure.malli-form :refer [malli-form]]))

(defmethod malli-form :s/sound [_ _schemas]
  :string)
