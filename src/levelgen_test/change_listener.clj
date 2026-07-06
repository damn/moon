(ns levelgen-test.change-listener
  (:require [gdx.scene2d.utils.change-listener :as change-listener]
            [levelgen-test.app-event :as app-event]))

(defn f [f]
  (change-listener/create
   (app-event/f f)))
