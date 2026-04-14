(ns moon.create.skin
  (:require [gdl.files :as files]
            [gdl.bitmap-font :as bitmap-font]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]))

(defn step
  [{:keys [ctx/files]
    :as ctx}
   path]
  (assoc ctx :ctx/skin (let [skin (skin/create (files/internal files path))]
                         (bitmap-font/enable-markup! (skin/font skin "default-font") true)
                         skin)))
