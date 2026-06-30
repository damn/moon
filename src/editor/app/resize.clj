(ns editor.app.resize
  (:require [clojure.gdx :as gdx]))

(defn resize!
  [{:keys [ctx/stage]} width height]
  (gdx/viewport-update (:stage/viewport stage) width height true))
