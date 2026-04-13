(ns moon.actor
  (:refer-clojure :exclude [name]))

(defprotocol Actor
  (name [_])
  (x [_])
  (y [_])
  (width [_])
  (height [_])
  (stage->local-coordinates [_ vector2])
  (add-listener! [_ listener])
  (user-object [_])
  (stage [_])
  (set-name! [_ name])
  (set-user-object! [_ object])
  (set-position! [_ [x y]]
                 [_ x y align])
  (set-visible! [_ visible?])
  (set-touchable! [_ touchable])
  (visible? [_])
  (hit [_ [x y] touchable?])
  (remove! [_])
  (parent [_]))

(defn toggle-visible! [actor]
  (set-visible! actor (not (visible? actor))))

(defn- ui-type->class [k]
  (case k
    :ui/window com.badlogic.gdx.scenes.scene2d.ui.Window))

(defn find-ancestor
  [actor ui-type-k]
  (if-let [parent (parent actor)]
    (if (instance? (ui-type->class ui-type-k) parent)
      parent
      (find-ancestor parent ui-type-k))
    (throw (Error. (str "Actor has no parent window " actor)))))
