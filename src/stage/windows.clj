(ns stage.windows
  (:require [clojure.gdx :as gdx]))

(defn create [ctx actor-fns]
  (let [group (gdx/group)]
    (run! #(gdx/add-actor! group %) (for [f actor-fns] (f ctx)))
    (doto group
      (Actor/.setName "moon.ui.windows"))))
