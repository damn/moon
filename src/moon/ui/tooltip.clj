(ns moon.ui.tooltip
  (:require [moon.ui.actor :as actor]
            [moon.ui.label :as label]
            [moon.ui.stage :as stage])
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextTooltip)))

(defn add! [actor tooltip-text ^Skin skin]
  (actor/add-listener! actor (TextTooltip. (str tooltip-text) skin))
  #_(tooltip/create {:update-fn (fn [tooltip]
                                (when-not (string? tooltip-text)
                                  (let [actor (tooltip/target tooltip)
                                        ctx (when-let [stage (actor/stage actor)]
                                              (stage/ctx stage))]
                                    (when ctx
                                      (tooltip/set-text! tooltip (tooltip-text ctx))))))
                   :target actor
                   :content (doto (label/create (if (string? tooltip-text) tooltip-text "")
                                                skin)
                              (label/set-alignment! align/center))})
  actor)

(defn remove! [actor]
  ; TODO ?
  #_(tooltip/remove! actor)

  )
