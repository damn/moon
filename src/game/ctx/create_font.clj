(ns game.ctx.create-font
  (:require [gdx.application :as app]
            [gdx.files :as files]
            [gdx.freetype :refer [generate-font]]))

(defn create-font
  [{:keys [ctx/app]}
   {:keys [path] :as config}]
  (generate-font (files/internal (app/files app) path)
                 config))
