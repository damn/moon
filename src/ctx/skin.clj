(ns ctx.skin
  (:require [files.internal :as internal]
            [bitmap-font.get-data :refer [get-data]]
            [scene2d.ui.skin.get-font :as get-font]
            [bitmap-font-data.enable-markup :refer [enable-markup!]]
            [file-handle.skin :as skin]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/f (internal/f files "skin/uiskin.json"))]
    (enable-markup! (get-data (get-font/f skin "default-font")))
    skin))
