(ns moon.ui.tooltip
  (:import (com.badlogic.gdx.scenes.scene2d.ui Skin
                                               TextTooltip)
           (moon Stage)))

(defn add! [actor tooltip-text ^Skin skin]
  (.addListener actor (TextTooltip. (str tooltip-text) skin))
  #_(tooltip/create {:update-fn (fn [tooltip]
                                (when-not (string? tooltip-text)
                                  (let [actor (tooltip/target tooltip)
                                        ctx (when-let [stage (.getStage actor)]
                                              (.ctx ^Stage stage))]
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
