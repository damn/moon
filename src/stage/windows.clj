(ns stage.windows
  (:require [clojure.gdx :as gdx]))

(defn create [ctx actor-fns]
  (let [group (gdx/group)]
    (run! #(gdx/add-actor! group %) (for [f actor-fns] (f ctx)))
    (doto group
      (com.badlogic.gdx.scenes.scene2d.Actor/.setName "moon.ui.windows"))))
