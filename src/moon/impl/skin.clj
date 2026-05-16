(ns moon.impl.skin
  (:import (com.badlogic.gdx Application)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn create
  [{:keys [^Application ctx/app]}]
  (let [skin (Skin. (.internal (.getFiles app) "uiskin.json"))]
    (set! (.markupEnabled (.getData (.getFont skin "default-font"))) true)
    skin))
