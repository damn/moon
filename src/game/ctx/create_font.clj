(ns game.ctx.create-font
  (:require [com.badlogic.gdx.application :as app]
            [com.badlogic.gdx.files :as files]
            [gdx.freetype :refer [generate-font]]))

(defn create-font
  [{:keys [ctx/app]}
   {:keys [path] :as config}]
  (generate-font (files/internal (app/files app) path)
                 config))
