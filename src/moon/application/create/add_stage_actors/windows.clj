(ns moon.application.create.add-stage-actors.windows
  (:require moon.application.create.add-stage-actors.windows.info
            moon.application.create.add-stage-actors.windows.inventory
            [clojure.gdx.scene2d.actor :as actor]))

(defn create [ctx]
  (actor/create
   {:type :ui/group
    :group/actors [(moon.application.create.add-stage-actors.windows.info/create ctx)
                   (moon.application.create.add-stage-actors.windows.inventory/create ctx)]
    :actor/name "moon.ui.windows"}))
