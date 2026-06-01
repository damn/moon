(ns stage.windows
  (:require [clojure.gdx.scene2d.group.create :refer [create-group]]))

(defn create [ctx actor-fns]
  (create-group
   {:group/actors (for [f actor-fns]
                    (f ctx))
    :actor/name "moon.ui.windows"}))
