(ns moon.ui.label
  (:require [gdl.ui.label :as label]
            [moon.ui :as ui]))

(defn create [text #_skin]
  (label/create text ui/skin))
