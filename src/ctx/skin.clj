(ns ctx.skin
  (:require [scene2d.ui.skin.get-font :as get-font])
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (Skin. (Files/.internal files "skin/uiskin.json"))]
    (set! (.markupEnabled (.getData (get-font/f skin "default-font"))) true)
    skin))
