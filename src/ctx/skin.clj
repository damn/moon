(ns ctx.skin
  (:import (com.badlogic.gdx Files)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn step
  [{:keys [ctx/files]}]
  (let [skin (Skin. (Files/.internal files "skin/uiskin.json"))]
    (set! (.markupEnabled (.getData (.getFont skin "default-font"))) true)
    skin))
