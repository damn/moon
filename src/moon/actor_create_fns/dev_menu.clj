(ns moon.actor-create-fns.dev-menu
  (:require [gdl.ui.stage :as stage]
            moon.application.create.ui.dev-menu-config))

(defn create
  [ctx]
  (moon.application.create.ui.dev-menu-config/create
   ctx
   nil #_(fn rebuild-actors! [stage ctx]
     (stage/clear! stage)
     ((requiring-resolve 'moon.application.create.add-actors/step) ctx)) ; remove
   nil #_(requiring-resolve 'moon.application.create.world/step); remove
   nil #_(requiring-resolve 'moon.application.open-editor/do!))) ; editor separte ... - javafx ? -
