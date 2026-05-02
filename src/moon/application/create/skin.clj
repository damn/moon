(ns moon.application.create.skin
  (:require [com.badlogic.gdx.application :as app])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/skin (let [skin (Skin. (.internal (app/files app) "uiskin.json"))]
                         (set! (.markupEnabled (.getData (.getFont skin "default-font"))) true)
                         skin)))
