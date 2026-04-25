(ns moon.start
  (:require [clojure.multifn :as multifn]
            [clojure.gdx.scene2d.ui.widget-group]
            [clojure.scene2d.actor]
            [clojure.utils :refer [edn-resource]]
            [moon.application]
            [moon.entity]
            [moon.state])
  (:gen-class))

(defn -main []
  (multifn/add-api-methods!
   {:required []
    :optional [#'moon.entity/create
               #'moon.entity/after-create
               #'moon.entity/destroy
               #'moon.entity/tick
               #'moon.entity/render]}
   (edn-resource "entity.edn"))
  (multifn/add-api-methods!
   {:required []
    :optional [#'moon.entity/create ; FIXME  2 create ! this unused !
               #'moon.entity/after-create ; not used here
               #'moon.entity/destroy ; not used etc. ?
               #'moon.entity/tick
               #'moon.entity/render

               #'moon.state/create
               #'moon.state/enter
               #'moon.state/exit
               #'moon.state/cursor
               #'moon.state/pause-game?
               #'moon.state/clicked-inventory-cell
               #'moon.state/draw-ui-view
               #'moon.state/handle-input]}
   (edn-resource "entity_state.edn"))
  (multifn/add-methods! #'clojure.scene2d.actor/create (edn-resource "actor_create.edn"))
  (moon.application/start!
   {
    :colors {"PRETTY_NAME" [0.84 0.8 0.52 1]}
    :title "Moon"
    :window {:width 1440
             :height 900}
    :fps 60
    }))
