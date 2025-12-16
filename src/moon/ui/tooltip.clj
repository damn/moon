(ns moon.ui.tooltip
  (:require [gdl.ui.actor :as actor]
            [gdl.ui.label :as label]
            [moon.ui :as ui]
            [moon.ui.label :as vis-label]
            [gdl.ui.stage :as stage]
            [gdl.ui.tooltip :as tooltip]
            [gdl.utils.align :as align]))

(defn add! [actor tooltip-text]
  (.addListener actor (tooltip/create tooltip-text ui/skin))
  #_(tooltip/create {:update-fn (fn [tooltip]
                                (when-not (string? tooltip-text)
                                  (let [actor (tooltip/target tooltip)
                                        ctx (when-let [stage (actor/stage actor)]
                                              (stage/ctx stage))]
                                    (when ctx
                                      (tooltip/set-text! tooltip (tooltip-text ctx))))))
                   :target actor
                   :content (doto (vis-label/create (if (string? tooltip-text) tooltip-text ""))
                              (label/set-alignment! align/center))})
  actor)

(defn remove! [actor]
  ; TODO ?
  #_(tooltip/remove! actor)

  )
