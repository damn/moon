(ns moon.actor-create-fns.dev-menu
  (:require [gdl.ui.stage :as stage]))

(defn create
  [{:keys [ctx/stage]
    :as ctx}]
  ((:dev-menu (.config stage))
   ctx
   (fn rebuild-actors! [stage ctx]
     (stage/clear! stage)
     ((requiring-resolve 'moon.application.create.add-actors/step) ctx))
   (requiring-resolve 'moon.application.create.world/step)
   (requiring-resolve 'moon.application.open-editor/do!)))
