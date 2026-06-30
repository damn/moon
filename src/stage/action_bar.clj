(ns stage.action-bar
  (:require [clojure.gdx :as gdx]
            [gdx.scenes.scene2d.ui.table :as table]))

(defn create [_ctx]
  (doto (table/create
         {:table/cell-defaults {:pad 2}
          :table/rows [[{:actor (doto (gdx/horizontal-group)
                                  (gdx/horizontal-group-space! 2)
                                  (gdx/horizontal-group-pad! 2)
                                  (gdx/set-name! "moon.ui.action-bar.horizontal-group")
                                  (gdx/set-user-object!
                                   (doto (gdx/button-group)
                                     (gdx/button-group-set-max-check-count! 1)
                                     (gdx/button-group-set-min-check-count! 0))))
                         :expand? true
                         :bottom? true}]]})
    (gdx/table-set-fill-parent! true)
    (gdx/set-name! "moon.ui.action-bar")))
