(ns moon.create.skin
  (:require [clj.api.com.badlogic.gdx.files :as files]
            [clj.api.com.badlogic.gdx.graphics.g2d.bitmap-font :as bitmap-font]
            [clj.api.com.badlogic.gdx.graphics.g2d.bitmap-font.data :as bitmap-font.data]
            [clj.api.com.badlogic.gdx.scenes.scene2d.ui.skin :as skin]))

(defn step
  [{:keys [ctx/files]
    :as ctx}
   path]
  (assoc ctx :ctx/skin (let [skin (skin/create (files/internal files path))]
                         (bitmap-font.data/enable-markup! (-> skin
                                                              (skin/font "default-font")
                                                              bitmap-font/data)
                                                          true)
                         skin)))
