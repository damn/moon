(ns editor.app.resize
  (:require [clojure.viewport.update :as update!]))

(defn resize!
  [{:keys [ctx/stage]} width height]
  (update!/f (:stage/viewport stage) width height true))
