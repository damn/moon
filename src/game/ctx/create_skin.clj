(ns game.ctx.create-skin
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.files :as files]
            [clojure.gdx.graphics.g2d.bitmap-font :as font]
            [clojure.gdx.graphics.g2d.bitmap-font.data :as font.data]
            [clojure.gdx.scene2d.ui.skin :as skin]))

(defn create-skin
  [{:keys [ctx/app]} path]
  (let [skin (skin/create (files/internal (app/files app) path))]
    (-> skin
        (skin/font "default-font")
        font/data
        font.data/enable-markup!)
    skin))
