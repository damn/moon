(ns clojure.malli-form-s-sound
  (:require [clojure.malli-form :refer [malli-form]]))

(defmethod malli-form :s/sound
  [_ _]
  :string)
