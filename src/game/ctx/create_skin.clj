(ns game.ctx.create-skin
  (:require [clojure.gdx.application :as app]
            [clojure.gdx.files :as files]
            [clojure.scene2d.ui.skin :as skin]))

(defn create-skin
  [{:keys [ctx/app]} path]
  (let [skin (skin/create (files/internal (app/files app) path))]
    (set! (.markupEnabled (-> skin
                              (skin/font "default-font")
                              .getData))
          true)
    skin))
