(ns moon.ui.select-box
  (:require [gdl.ui.select-box :as select-box]
            [moon.ui :as ui]))

(defn create [{:keys [items selected] :as opts}]
  (select-box/create opts ui/skin))

(def selected select-box/selected)
