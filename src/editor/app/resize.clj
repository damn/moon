(ns editor.app.resize
  (:require [com.badlogic.gdx.utils.viewport.update :as update!]))

(defn resize!
  [{:keys [ctx/stage]} width height]
  (update!/f (:stage/viewport stage) width height true))
