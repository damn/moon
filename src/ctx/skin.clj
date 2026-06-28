(ns ctx.skin
  (:require [files.internal :as internal]
            [scene2d.ui.skin.get-font :as get-font]
            [bitmap-font-data.enable-markup :refer [enable-markup!]]
            [file-handle.skin :as skin]))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (skin/f (internal/f files "skin/uiskin.json"))]
    (enable-markup! (.getData (get-font/f skin "default-font")))
    skin))
