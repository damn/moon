(ns moon.impl.skin
  (:require [clojure.gdx.app :as app]
            [clojure.gdx.files :as files])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn create
  [{:keys [ctx/app]}]
  (let [skin (Skin. (files/internal (app/files app) "uiskin.json"))]
    (set! (.markupEnabled (.getData (.getFont skin "default-font"))) true)
    skin))
