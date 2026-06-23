(ns editor.app.resize
  (:require [viewport.update :as update!]))

(defn resize!
  [{:keys [ctx/stage]} width height]
  (update!/f (:stage/viewport stage) width height true))
