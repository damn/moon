(ns com.badlogic.gdx.scenes.scene2d.actor
  (:require [com.badlogic.gdx.math.vector2 :as vector2]
            [com.badlogic.gdx.scenes.scene2d.touchable :as touchable]
            [com.badlogic.gdx.scenes.scene2d.ui.text-tooltip :as text-tooltip]
            [com.badlogic.gdx.scenes.scene2d.utils.change-listener :as change-listener]
            [com.badlogic.gdx.scenes.scene2d.utils.click-listener :as click-listener]
            [com.badlogic.gdx.utils.align :as align]
            [gdl.scene2d.actor :as actor])
  (:import (com.badlogic.gdx.scenes.scene2d Actor)))

(defmethod actor/create :ui/actor
  [{:keys [act! draw!] :as opts}]
  (doto (proxy [Actor] []
          (act [delta]
            (when act!
              (act! this delta))
            (let [^Actor this this]
              (proxy-super act delta)))
          (draw [batch parent-alpha]
            (when draw!
              (draw! this batch parent-alpha))))
    (actor/set-opts! opts)))

(extend-type Actor
  actor/Actor
  (name [actor]
    (.getName actor))

  (x [actor]
    (.getX actor))

  (y [actor]
    (.getY actor))

  (width [actor]
    (.getWidth actor))

  (height [actor]
    (.getHeight actor))

  (user-object [actor]
    (.getUserObject actor))

  (stage [actor]
    (.getStage actor))

  (set-name! [actor name]
    (.setName actor name))

  (set-user-object! [actor object]
    (.setUserObject actor object))

  (visible? [actor]
    (.isVisible actor))

  (hit [^Actor actor [x y] touchable?]
    (.hit actor x y touchable?))

  (remove! [actor]
    (.remove actor))

  (parent [actor]
    (.getParent actor))

  (set-position!
    ([actor [x y]]
     (.setPosition actor x y))
    ([actor x y align]
     (.setPosition actor x y (align/k->value align))))

  (set-visible! [actor visible?]
    (.setVisible actor visible?))

  (set-touchable! [actor touchable]
    (.setTouchable actor (touchable/k->value touchable)))

  (add-listener! [actor [listener-k listener-params]]
    (.addListener actor
                  (case listener-k
                    :listener/change (change-listener/create listener-params)
                    :listener/text-tooltip (text-tooltip/create listener-params)
                    :listener/click (click-listener/create listener-params))))

  (stage->local-coordinates [actor xy]
    (vector2/->clj (.stageToLocalCoordinates actor (vector2/->java xy))))

  (find-ancestor [actor pred]
    (if-let [p (actor/parent actor)]
      (if (pred p)
        p
        (actor/find-ancestor p pred))
      (throw (Error. (str "Actor has no parent window " actor)))))

  (toggle-visible! [actor]
    (actor/set-visible! actor (not (actor/visible? actor))))

  (set-opts! [actor opts]
    (when-let [user-object (:actor/user-object opts)]
      (actor/set-user-object! actor user-object))

    (when (:actor/position opts)
      (let [[x y align] (:actor/position opts)]
        (if align
          (actor/set-position! actor x y align)
          (actor/set-position! actor [x y]))))

    (when (contains? opts :actor/visible?)
      (actor/set-visible! actor (:actor/visible? opts)))

    (when-let [touchable (:actor/touchable opts)]
      (actor/set-touchable! actor touchable))

    (when-let [name (:actor/name opts)]
      (actor/set-name! actor name))

    (when-let [listeners (:actor/listeners opts)]
      (doseq [listener listeners]
        (actor/add-listener! actor listener)))))
