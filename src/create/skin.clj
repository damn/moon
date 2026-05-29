(ns create.skin
  (:require [gdx.application :as app]
            [gdx.files :as files])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin)))

(defn step
  [{:keys [ctx/app]
    :as ctx}]
  (assoc ctx :ctx/skin
         (let [skin (Skin. (files/internal (app/files app) "uiskin.json"))]
           (set! (.markupEnabled (-> skin
                                     (.getFont "default-font")
                                     .getData))
                 true)
           skin)))
