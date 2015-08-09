package io.jfc.generic

import io.jfc.{ Decoder, JsonObject, ObjectEncoder }
import shapeless.LabelledGeneric

/**
 * Semi-automatic codec derivation.
 *
 * This object provides helpers for creating [[io.jfc.Decoder]] and [[io.jfc.Encoder]] instances
 * for tuples, case classes, "incomplete" case classes, sealed trait hierarchies, etc.
 *
 * Typical usage will look like the following:
 *
 * {{{
 *   import io.jfc._, io.jfc.generic.semiauto._, io.jfc.generic.semiauto.tuple._
 *
 *   case class Foo(i: Int, p: (String, Double))
 *
 *   object Foo {
 *     implicit val decodeFoo: Decoder[Foo] = deriveFor[Foo].decoder
 *     implicit val encodeFoo: Encoder[Foo] = deriveFor[Foo].encoder
 *   }
 * }}}
 */
object semiauto
  extends BaseInstances
  with LabelledInstances
  with HListInstances {
  object tuple extends TupleInstances
  object incomplete extends IncompleteInstances

  def deriveFor[A]: DerivationHelper[A] = new DerivationHelper[A]

  class DerivationHelper[A] {
    def encoder[R](implicit
      gen: LabelledGeneric.Aux[A, R],
      e: ObjectEncoder[R]
    ): ObjectEncoder[A] = new ObjectEncoder[A] {
      def encodeObject(a: A): JsonObject = e.encodeObject(gen.to(a))
    }

    def decoder[R](implicit
      gen: LabelledGeneric.Aux[A, R],
      d: Decoder[R]
    ): Decoder[A] = d.map(gen.from)
  }
}
