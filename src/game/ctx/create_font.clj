(ns game.ctx.create-font
  (:require [com.badlogic.gdx.files :as files]
            [gdx.freetype :refer [generate-font]]))

(defn create-font
  [{:keys [ctx/files]}
   {:keys [path] :as config}]
  (generate-font (files/internal files path)
                 config))
