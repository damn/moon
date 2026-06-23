(ns ctx.skin
  (:require [gdl.internal :as internal]
            [bitmap-font.get-data :refer [get-data]]
            [ui.skin.get-font :as get-font]
            [gdl.enable-markup :refer [enable-markup!]]
            [ui.skin :as skin]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/f (internal/f files "skin/uiskin.json"))]
    (enable-markup! (get-data (get-font/f skin "default-font")))
    skin))
