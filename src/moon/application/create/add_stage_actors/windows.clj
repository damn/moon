(ns moon.application.create.add-stage-actors.windows
  (:require [clojure.gdx.scene2d.actor :as actor]))

(defn create [ctx actor-fns]
  (actor/create
   {:type :ui/group
    :group/actors (for [f actor-fns]
                    (f ctx))
    :actor/name "moon.ui.windows"}))
