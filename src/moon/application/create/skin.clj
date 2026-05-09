(ns moon.application.create.skin
  (:import (com.badlogic.gdx Application)
           (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/skin (let [skin (Skin. (.internal (.getFiles app) "uiskin.json"))]
                         (set! (.markupEnabled (.getData (.getFont skin "default-font"))) true)
                         skin)))
